CREATE TABLE `system_variable` (
  `id` bigint(20) NOT NULL,
  `deadline` date DEFAULT NULL COMMENT '日报修改最后期限',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局数据';
