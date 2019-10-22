package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.user.cache.VerifyCache;
import com.xxx.user.form.*;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.security.GenericLogin;
import com.xxx.user.service.AccountService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.service.VerifyService;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.JavaValidate;
import com.xxx.utils.MD5Utils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.PubUserBase;
import com.xxx.model.business.PubUserLogin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 账户管理
 */
@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private VerifyService verifyService;

	/**
	 * 登录
	 * @param request
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login", method = {RequestMethod.POST})
	public ResponseEntity merchantLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginUserForm loginUser) throws Exception {
		if (StringUtils.isBlank(loginUser.usercode))
			return new ResponseEntity(new RestResponseEntity(110, "用户名为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(loginUser.password))
			return new ResponseEntity(new RestResponseEntity(120, "密码为空", null), HttpStatus.OK);
		if (loginUser.loginType == null)
			return new ResponseEntity(new RestResponseEntity(130, "登录身份不能为空", null), HttpStatus.OK);
		String pass = loginUser.password;
		pass = MD5Utils.md5Hex(pass);
		GenericLogin genericLogin = accountService.processMerchantLogin(loginUser.usercode, pass, loginUser.loginType);
		//如果账户正常而且是供应商，就存到session中
		if (genericLogin.userStatus == 0 && loginUser.loginType == 3) {
			SessionUtils.setSession(response, SessionUtils.SUPPLIERID, 3600 * 7, genericLogin.getSuplierId());
		}
		SessionUtils.setSession(response, SessionUtils.TOKEN, 3600 * 7, CurrentUser.generateAuthToken(genericLogin));
		return returnAuthToken(genericLogin);
	}

	private ResponseEntity returnAuthToken(GenericLogin genericLogin) {
		if (genericLogin == null) {
			return new ResponseEntity(new RestResponseEntity(130, "用户名或密码不正确", null), HttpStatus.OK);
		} else {
			JSONObject data = new JSONObject();
			data.put("userName", genericLogin.userName);
			data.put("loginType", genericLogin.loginType);
			//这里装的就是用户的key和用户信息装成了十六进制之后大写的加密内容
			data.put("authToken", CurrentUser.generateAuthToken(genericLogin));
			data.put("userStatus", genericLogin.userStatus);
			return new ResponseEntity(new RestResponseEntity(100, null, data), HttpStatus.OK);
		}
	}

	/**
	 * 注册
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/register", method = {RequestMethod.POST})
	public ResponseEntity register(HttpServletRequest request, @RequestBody RegisterUserForm form) throws UpsertException, ResponseEntityException {
		if (StringUtils.isBlank(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(110, "手机号码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(form.verificationCode))
			return new ResponseEntity(new RestResponseEntity(130, "验证码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(form.password))
			return new ResponseEntity(new RestResponseEntity(160, "密码不能为空", null), HttpStatus.OK);
		if (form.loginType == null)
			return new ResponseEntity(new RestResponseEntity(190, "登录身份不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		if(!form.password.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(170, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if(!form.password.matches("^[0-9a-zA-Z_]{6,12}$"))
			return new ResponseEntity(new RestResponseEntity(170, "密码必须为6-12位字符", null), HttpStatus.OK);
		if(form.userCode != null && !form.userCode.matches("^[0-9a-zA-Z_]{6,12}$"))
			return new ResponseEntity(new RestResponseEntity(180, "用户名必须为6-12位字母、数字、下划线组成", null), HttpStatus.OK);
		verifyService.checkVerificationCode(form.phoneNumber, form.verificationCode);
		GenericLogin genericLogin = accountService.saveRegisterUser(form.userCode, form.phoneNumber, MD5Utils.md5Hex(form.password), form.loginType, form.userType, null, null);
		VerifyCache.deleteVerificationCode(form.phoneNumber);
		return returnAuthToken(genericLogin);
	}


	/**
	 * 通过手机验证码获取身份信息， 不存在则注册
	 * @param request
	 * @param form
	 * @return
	 * @throws UpsertException
	 * @throws ResponseEntityException
	 */
	@RequestMapping(value = "/verifyLogin", method = {RequestMethod.POST})
	public ResponseEntity verifyLogin(HttpServletRequest request, @RequestBody RegisterUserForm form) throws UpsertException, ResponseEntityException {
		if (StringUtils.isBlank(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(110, "手机号码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(form.verificationCode))
			return new ResponseEntity(new RestResponseEntity(130, "验证码不能为空", null), HttpStatus.OK);
		if (form.loginType == null)
			return new ResponseEntity(new RestResponseEntity(190, "登录身份不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		verifyService.checkVerificationCode(form.phoneNumber, form.verificationCode);
		GenericLogin genericLogin;
		//判断账户是否存在
		PubUserLogin userLogin = accountService.getPubUserLogin(form.phoneNumber, form.phoneNumber, form.loginType);
		if (userLogin != null) {
			genericLogin = accountService.returnGenericLogin(userLogin, userLogin.getPubUserBase() == null ? userLogin.getUserCode() : userLogin.getPubUserBase().getName());
		} else {
			//注册
			genericLogin = accountService.saveRegisterUser(form.phoneNumber, form.phoneNumber, MD5Utils.md5Hex("888888"), form.loginType, form.userType, null, null);
			//发送短信
			SmsUtils.sendRegisterMessage(form.phoneNumber, form.phoneNumber, "888888");
		}
		VerifyCache.deleteVerificationCode(form.phoneNumber);
		return returnAuthToken(genericLogin);
	}


	/**
	 * 重置密码
	 * @param resetUserForm
	 * @return
	 */
	@RequestMapping(value = "/resetPassword", method = {RequestMethod.POST})
	public ResponseEntity resetPassword(HttpServletRequest request, @RequestBody ResetUserForm resetUserForm) throws UpsertException, ResponseEntityException {
		if (StringUtils.isBlank(resetUserForm.phonenumber))
			return new ResponseEntity(new RestResponseEntity(110, "手机号码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(resetUserForm.verificationCode))
			return new ResponseEntity(new RestResponseEntity(130, "验证码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(resetUserForm.password))
			return new ResponseEntity(new RestResponseEntity(160, "密码不能为空", null), HttpStatus.OK);
		if (resetUserForm.loginType == null)
			return new ResponseEntity(new RestResponseEntity(190, "身份类型不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(resetUserForm.phonenumber))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		if(!resetUserForm.password.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(170, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if(!resetUserForm.password.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(170, "密码必须为6-16位字符", null), HttpStatus.OK);
		verifyService.checkVerificationCode(resetUserForm.phonenumber, resetUserForm.verificationCode);
		PubUserLogin userLogin = accountService.getPubUserLogin(resetUserForm.phonenumber, resetUserForm.phonenumber, resetUserForm.loginType);
		if (userLogin == null)
			return new ResponseEntity(new RestResponseEntity(180, "账号不存在", null), HttpStatus.OK);
		accountService.updatePassword(userLogin.getId(), MD5Utils.md5Hex(resetUserForm.password));
		VerifyCache.deleteVerificationCode(resetUserForm.phonenumber);
		return new ResponseEntity(new RestResponseEntity(100, "发送成功", null), HttpStatus.OK);
	}


	/**
	 * @Description: 获取账户信息
	 * @Author: Chen.zm
	 * @Date: 2017/11/4 0004
	 */
	@RequestMapping(value = "/loginInfo", method = {RequestMethod.POST})
	public ResponseEntity loginInfo() throws Exception {
		PubUserBase base = accountService.get2(PubUserBase.class, "userId", CurrentUser.get().userId);
		if (base == null) base = new PubUserBase();
		JSONObject json = new JSONObject();
		json.put("name", base.getName());
		json.put("icon", OSSClientUtil.getObjectUrl(base.getIcon()));
		json.put("mobile", base.getMobile());
		json.put("sex", base.getSex());
		PubUserLogin userLogin = accountService.get2(PubUserLogin.class, CurrentUser.get().userId);
		json.put("userCode", userLogin.getUserCode());
		GenericLogin genericLogin = accountService.returnGenericLogin(userLogin, base.getName());
		json.put("userStatus", genericLogin.userStatus);
		return new ResponseEntity(new RestResponseEntity(100, null, json), HttpStatus.OK);
	}


	/**
	 * @Description: 根据用户名 获取用户信息
	 * @Author: Chen.zm
	 * @Date: 2017/11/4 0004
	 */
	@RequestMapping(value = "/findUserPhone", method = {RequestMethod.POST})
	public ResponseEntity findUserPhone(@RequestBody UserCodeForm form) throws Exception {
		if (StringUtils.isBlank(form.userCode))
			return new ResponseEntity(new RestResponseEntity(110, "用户名不能为空", null), HttpStatus.OK);
		if (form.loginType == null)
			return new ResponseEntity(new RestResponseEntity(140, "身份类型不能为空", null), HttpStatus.OK);
//		PubUserLogin userLogin = accountService.get2(PubUserLogin.class, "userCode", form.userCode, "loginType", form.loginType);
		PubUserLogin userLogin = accountService.getPubUserLogin(form.userCode, form.userCode, form.loginType);
		if (userLogin == null)
			return new ResponseEntity(new RestResponseEntity(120, "用户不存在", null), HttpStatus.OK);
		PubUserBase base = accountService.get2(PubUserBase.class, "userId", userLogin.getId());
		if (base == null)
			return new ResponseEntity(new RestResponseEntity(130, "用户信息不存在", null), HttpStatus.OK);
		JSONObject json = new JSONObject();
		json.put("userCode", userLogin.getUserCode());
		json.put("mobile", base.getMobile());
		return new ResponseEntity(new RestResponseEntity(100, null, json), HttpStatus.OK);
	}


	/**
	 * @Description: 修改名称
	 * @Author: Chen.zm
	 * @Date: 2017/11/8 0008
	 */
	@RequestMapping(value = "/updateName", method = {RequestMethod.POST})
	public ResponseEntity updateName(@RequestBody NameForm form) throws Exception {
		if (StringUtils.isBlank(form.name))
			return new ResponseEntity(new RestResponseEntity(110, "名称不能为空", null), HttpStatus.OK);
		accountService.updateName(CurrentUser.get().userId, form.name);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}

	/**
	 * @Description: 修改头像
	 * @Author: Chen.zm
	 * @Date: 2017/11/8 0008
	 */
	@RequestMapping(value = "/updateIcon", method = {RequestMethod.POST})
	public ResponseEntity updateIcon(@RequestBody IconForm form) throws Exception {
		if (StringUtils.isBlank(form.icon))
			return new ResponseEntity(new RestResponseEntity(110, "头像不能为空", null), HttpStatus.OK);
		accountService.updateIcon(CurrentUser.get().userId, form.icon);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}


	/**
	 * @Description: 修改用户资料
	 * @Author: Chen.zm
	 * @Date: 2017/11/16 0016
	 */
	@RequestMapping(value = "/updateUserBase", method = {RequestMethod.POST})
	public ResponseEntity updateUserBase(@RequestBody UserBaseForm form) throws Exception {
		if (StringUtils.isBlank(form.phone))
			return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(form.phone))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		verifyService.checkVerificationCode(form.phone, form.verificationCode);
		accountService.updateUserBase(CurrentUser.get().userId, form.name, form.sex, form.phone, form.icon);
		VerifyCache.deleteVerificationCode(form.phone);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}

	/**
	 * @Description:修改密码
	 * @Author: hanchao
	 * @Date: 2018/1/22 0022
	 */
	@RequestMapping(value = "/updatePassword", method = {RequestMethod.POST})
	public ResponseEntity updatePassword(@RequestBody PasswordForm form) throws Exception {
		if (!form.password.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(110, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.password.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(120, "密码必须为6-16位字符", null), HttpStatus.OK);
		if (!form.passwordSure.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(130, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.passwordSure.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(140, "密码必须为6-16位字符", null), HttpStatus.OK);
		if (!form.passwordSure1.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(150, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.passwordSure1.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(160, "密码必须为6-16位字符", null), HttpStatus.OK);
		PubUserLogin user = accountService.get2(PubUserLogin.class,"id",CurrentUser.get().userId,"userPassword",MD5Utils.md5Hex(form.password),"logicDeleted",false);
		if(user == null){
			return new ResponseEntity(new RestResponseEntity(170, "原密码输入错误", null), HttpStatus.OK);
		}
		if(!form.passwordSure.equals(form.passwordSure1)){
			return new ResponseEntity(new RestResponseEntity(180, "新密码输入不一致", null), HttpStatus.OK);
		}
		accountService.updatePassword(CurrentUser.get().userId,MD5Utils.md5Hex(form.passwordSure));
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}


}
