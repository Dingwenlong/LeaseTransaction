SET @dbname = DATABASE();
SET @tablename = 'user';
SET @columnname = 'password';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  'ALTER TABLE `user` ADD COLUMN `password` VARCHAR(255) DEFAULT NULL COMMENT ''密码哈希'' AFTER `openid`'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
