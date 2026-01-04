--liquibase formatted sql
--changeset tricol-user:9-add-cost-fields-to-goods-issues
ALTER TABLE goods_issues ADD COLUMN total_amount DOUBLE;

