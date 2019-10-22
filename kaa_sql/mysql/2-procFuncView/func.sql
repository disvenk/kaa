delimiter $$





-- 获取指定时间内的供应商订单总数
drop function if exists get_sup_sales_order_count$$
create function get_sup_sales_order_count(suplierId int, dateStar varchar(30), dateEnd varchar(30), producedStatus int)
returns int
begin
declare count int;

	Select count(1) INTO count from sup_sales_order Where logicDeleted = 0 And suplier_id = suplierId
	And IF (producedStatus is NULL, 0=0, produced_status = producedStatus)
	And IF (dateStar is NULL, 0=0, createdDate >= dateStar)
	And IF (dateEnd is NULL, 0=0, createdDate <= dateEnd);

return count;
end$$
-- select get_sup_sales_order_count(1,'2018-01-10', NULL, 2)


-- 获取指定时间内的供应商订单总金额
drop function if exists get_sup_sales_order_total_sum$$
create function get_sup_sales_order_total_sum(suplierId int, dateStar varchar(30), dateEnd varchar(30), producedStatus int)
returns double
begin
declare sum double;

	Select IFNULL(sum(total), 0) INTO sum from sup_sales_order Where logicDeleted = 0 And suplier_id = suplierId
	And IF (producedStatus is NULL, 0=0, produced_status = producedStatus)
	And IF (dateStar is NULL, 0=0, createdDate >= dateStar)
	And IF (dateEnd is NULL, 0=0, createdDate <= dateEnd);

return sum;
end$$
-- select get_sup_sales_order_total_sum(1,NULL, NULL, 2)




-- 获取指定时间内的供应商 工单 总数
drop function if exists get_sup_order_count$$
create function get_sup_order_count(suplierId int, dateStar varchar(30), dateEnd varchar(30), producedStatus int)
returns int
begin
declare count int;

	Select count(1) INTO count from sup_order Where logicDeleted = 0 And suplier_id = suplierId
	And IF (producedStatus is NULL, 0=0, produced_status = producedStatus)
	And IF (dateStar is NULL, 0=0, createdDate >= dateStar)
	And IF (dateEnd is NULL, 0=0, createdDate <= dateEnd);

return count;
end$$
-- select get_sup_order_count(1,'2018-01-10', NULL, 2)


-- 获取指定时间内的供应商 工单 总金额
drop function if exists get_sup_order_total_sum$$
create function get_sup_order_total_sum(suplierId int, dateStar varchar(30), dateEnd varchar(30), producedStatus int)
returns double
begin
declare sum double;

	Select IFNULL(sum(total), 0) INTO sum from sup_order Where logicDeleted = 0 And suplier_id = suplierId
	And IF (producedStatus is NULL, 0=0, produced_status = producedStatus)
	And IF (dateStar is NULL, 0=0, createdDate >= dateStar)
	And IF (dateEnd is NULL, 0=0, createdDate <= dateEnd);

return sum;
end$$
-- select get_sup_order_total_sum(1,NULL, NULL, 2)