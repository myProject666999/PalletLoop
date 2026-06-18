CREATE DATABASE IF NOT EXISTS pallet_loop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pallet_loop;

CREATE TABLE IF NOT EXISTS equipment_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL COMMENT '器具类型名称(托盘/周转箱/料架等)',
    code VARCHAR(32) NOT NULL COMMENT '类型编码',
    unit VARCHAR(16) NOT NULL DEFAULT '个' COMMENT '计量单位',
    deposit_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '押金金额',
    overdue_days INT NOT NULL DEFAULT 30 COMMENT '超期天数阈值',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='器具类型表';

CREATE TABLE IF NOT EXISTS partner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL COMMENT '合作方名称',
    code VARCHAR(32) NOT NULL COMMENT '合作方编码',
    type VARCHAR(16) NOT NULL COMMENT '类型: CUSTOMER/SUPPLIER',
    contact_name VARCHAR(64) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(256) DEFAULT NULL COMMENT '地址',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='合作方表';

CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_id BIGINT NOT NULL COMMENT '器具类型ID',
    batch_no VARCHAR(64) NOT NULL COMMENT '批次号',
    quantity INT NOT NULL DEFAULT 0 COMMENT '数量',
    in_stock INT NOT NULL DEFAULT 0 COMMENT '在库数量',
    out_stock INT NOT NULL DEFAULT 0 COMMENT '在外数量',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_id (type_id),
    INDEX idx_batch_no (batch_no)
) ENGINE=InnoDB COMMENT='器具台账表';

CREATE TABLE IF NOT EXISTS dispatch_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispatch_no VARCHAR(64) NOT NULL COMMENT '发货单号',
    partner_id BIGINT NOT NULL COMMENT '合作方ID',
    equipment_id BIGINT NOT NULL COMMENT '器具ID',
    type_id BIGINT NOT NULL COMMENT '器具类型ID',
    quantity INT NOT NULL COMMENT '发出数量',
    order_no VARCHAR(64) DEFAULT NULL COMMENT '关联订单号',
    dispatch_date DATE NOT NULL COMMENT '发货日期',
    expected_return_date DATE DEFAULT NULL COMMENT '预计回收日期',
    operator VARCHAR(64) DEFAULT NULL COMMENT '操作人',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_partner_id (partner_id),
    INDEX idx_dispatch_no (dispatch_no),
    INDEX idx_dispatch_date (dispatch_date)
) ENGINE=InnoDB COMMENT='发货发出记录表';

CREATE TABLE IF NOT EXISTS return_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_no VARCHAR(64) NOT NULL COMMENT '回收单号',
    dispatch_id BIGINT NOT NULL COMMENT '关联发货记录ID',
    partner_id BIGINT NOT NULL COMMENT '合作方ID',
    equipment_id BIGINT NOT NULL COMMENT '器具ID',
    type_id BIGINT NOT NULL COMMENT '器具类型ID',
    quantity INT NOT NULL COMMENT '回收数量',
    damaged INT NOT NULL DEFAULT 0 COMMENT '损坏数量',
    return_date DATE NOT NULL COMMENT '回收日期',
    operator VARCHAR(64) DEFAULT NULL COMMENT '操作人',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_partner_id (partner_id),
    INDEX idx_return_no (return_no),
    INDEX idx_dispatch_id (dispatch_id)
) ENGINE=InnoDB COMMENT='回收记录表';

CREATE TABLE IF NOT EXISTS partner_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id BIGINT NOT NULL COMMENT '合作方ID',
    type_id BIGINT NOT NULL COMMENT '器具类型ID',
    dispatched INT NOT NULL DEFAULT 0 COMMENT '累计发出数量',
    returned INT NOT NULL DEFAULT 0 COMMENT '累计回收数量',
    damaged INT NOT NULL DEFAULT 0 COMMENT '累计损坏数量',
    outstanding INT NOT NULL DEFAULT 0 COMMENT '在外结存数量',
    turnover_count INT NOT NULL DEFAULT 0 COMMENT '周转次数',
    last_dispatch_date DATE DEFAULT NULL COMMENT '最近发出日期',
    last_return_date DATE DEFAULT NULL COMMENT '最近回收日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_partner_type (partner_id, type_id)
) ENGINE=InnoDB COMMENT='合作方器具结存表';
