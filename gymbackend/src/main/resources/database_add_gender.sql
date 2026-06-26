-- 添加性别字段到用户表
-- 此脚本会检查字段是否存在，如果不存在才添加，避免重复添加错误

USE gymdb;

-- 检查字段是否存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = 'users';
SET @columnname = 'gender';

-- 检查字段是否已存在
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = @tablename
    AND COLUMN_NAME = @columnname
);

-- 如果字段不存在，则添加
SET @sql = IF(@column_exists = 0,
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(10) COMMENT ''性别: MALE, FEMALE, OTHER'' AFTER bio'),
  'SELECT ''Column gender already exists, skipping...'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示结果
SELECT 
  CASE 
    WHEN @column_exists > 0 THEN '字段 gender 已存在，无需添加'
    ELSE '字段 gender 添加成功'
  END AS result;








