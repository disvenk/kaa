 delimiter $$

--没用到
-- 门店销售订单号生成
drop procedure if exists generate_store_sales_number$$
CREATE  PROCEDURE `generate_store_sales_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='STORE_SALES_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='STORE_SALES_NO_SHI_SEQ';
    commit;

end$$

-- 门店采购订单号生成
drop procedure if exists generate_store_suplier_number$$
CREATE  PROCEDURE `generate_store_suplier_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='STORE_SUPLIER_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='STORE_SUPLIER_NO_SHI_SEQ';
    commit;

end$$


-- 工单号生成
drop procedure if exists generate_sup_order_number$$
CREATE  PROCEDURE `generate_sup_order_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='SUP_ORDER_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='SUP_ORDER_NO_SHI_SEQ';
    commit;

end$$


-- 盒子购买订单号生成
drop procedure if exists generate_box_pay_order_number$$
CREATE  PROCEDURE `generate_box_pay_order_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='BOX_PAY_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='BOX_PAY_NO_SHI_SEQ';
    commit;

end$$

-- 盒子使用记录号生成
drop procedure if exists generate_box_use_log_number$$
CREATE  PROCEDURE `generate_box_use_log_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='BOX_USE_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='BOX_USE_NO_SHI_SEQ';
    commit;

end$$



-- 供应商首页统计数据
drop procedure if exists supplier_home_info$$
create procedure `supplier_home_info`(suplierId int(11))
begin

set @pre0Date = date_sub(curdate(),interval -1 day); -- 明日日期
set @pre1Date = date_sub(curdate(),interval 0 day); -- 今日日期
set @pre2Date = date_sub(curdate(),interval 1 day); -- 昨天日期
set @pre0DateMonthStr = date_add(curdate(),interval -day(curdate())+1 day); 	-- 本月第一天
set @pre0DateMonthEnd = last_day(curdate());	-- 本月最后一天
set @pre1DateMonthStr = date_add(curdate()-day(curdate())+1,interval -1 month); 	-- 上月第一天
set @pre2DateMonthEnd = last_day(date_sub(curdate(),interval 1 MONTH));	-- 上月最后一天

set @currentSalesOrderCount = 0; set @currentSalesOrderSum = 0; -- 本月订单总数，总金额
set @lastSalesOrderCount = 0; set @lastSalesOrderSum = 0; -- 上月订单总数，总金额
set @salesOrderCountBy2 = 0; set @salesOrderCountBy3 = 0; -- 待生产订单，生产中订单
set @currentSalesOrderCountBy7 = 0; set @lastSalesOrderCountBy7 = 0; -- 今日完成订单，昨日完成订单
set @orderUrgent = 0; -- 紧急工单
set @currentOrderCount = 0; set @currentOrderSum = 0; -- 本月工单总数，总金额
set @lastOrderCount = 0; set @lastOrderSum = 0; -- 上月工单总数，总金额


	select get_sup_sales_order_count(suplierId, @pre0DateMonthStr, @pre0DateMonthEnd, null) INTO @currentSalesOrderCount;
	select get_sup_sales_order_total_sum(suplierId, @pre0DateMonthStr, @pre0DateMonthEnd, null) INTO @currentSalesOrderSum;
	select get_sup_sales_order_count(suplierId, @pre1DateMonthStr, @pre2DateMonthEnd, null) INTO @lastSalesOrderCount;
	select get_sup_sales_order_total_sum(suplierId, @pre1DateMonthStr, @pre2DateMonthEnd, null) INTO @lastSalesOrderSum;

	select get_sup_sales_order_count(suplierId, null, null, 2) INTO @salesOrderCountBy2;
	select get_sup_sales_order_count(suplierId, null, null, 3) INTO @salesOrderCountBy3;

	SELECT COUNT(1) INTO @orderUrgent FROM sup_order WHERE logicDeleted = 0 And suplier_id = suplierId And urgent  = 2;

	select get_sup_sales_order_count(suplierId, @pre1Date, @pre0Date, 7) INTO @currentSalesOrderCountBy7;
	select get_sup_sales_order_count(suplierId, @pre2Date, @pre1Date, 7) INTO @lastSalesOrderCountBy7;

	select get_sup_order_count(suplierId, @pre0DateMonthStr, @pre0DateMonthEnd, null) INTO @currentOrderCount;
	select get_sup_order_total_sum(suplierId, @pre0DateMonthStr, @pre0DateMonthEnd, null) INTO @currentOrderSum;
	select get_sup_order_count(suplierId, @pre1DateMonthStr, @pre2DateMonthEnd, null) INTO @lastOrderCount;
	select get_sup_order_total_sum(suplierId, @pre1DateMonthStr, @pre2DateMonthEnd, null) INTO @lastOrderSum;

	SELECT @currentSalesOrderCount As currentSalesOrderCount, @currentSalesOrderSum As currentSalesOrderSum
	,@lastSalesOrderCount As lastSalesOrderCount, @lastSalesOrderSum As lastSalesOrderSum
	,@salesOrderCountBy2 As salesOrderCountBy2, @salesOrderCountBy3 As salesOrderCountBy3, @orderUrgent As orderUrgent
	,@currentSalesOrderCountBy7 As currentSalesOrderCountBy7, @lastSalesOrderCountBy7 As lastSalesOrderCountBy7
	,@currentOrderCount As currentOrderCount, @currentOrderSum As currentOrderSum
	,@lastOrderCount As lastOrderCount, @lastOrderSum As lastOrderSum;

end$$

-- call supplier_home_info(1);




-- 供应商注册 初始化数据  同步supplierId = 1 的数据
drop procedure if exists supplier_product_init$$
CREATE PROCEDURE `supplier_product_init`(suplierId int(11))
begin
	declare tabId int;

	INSERT INTO sup_product_base (name, remarks, sort, suplier_id, type, description)
	SELECT name, remarks, sort, suplierId, type, description FROM sup_product_base WHERE suplier_id = 1;
	select max(id) + 1 from sup_product_base INTO tabId;
	update SYS_Table_Generator set value = tabId where `name`= 'sup_product_base';

	INSERT INTO sup_procedure (name, price, remarks, sort, suplier_id)
	SELECT name, price, remarks, sort, suplierId FROM sup_procedure WHERE suplier_id = 1;
	select max(id) + 1 from sup_procedure INTO tabId;
	update SYS_Table_Generator set value = tabId where `name`= 'sup_procedure';

end$$

-- call supplier_product_init(3);