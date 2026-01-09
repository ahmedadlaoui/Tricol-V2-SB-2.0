--liquibase formatted sql
--changeset tricol-user:10-add-cost-fields-to-goods-issue-lines
ALTER TABLE goods_issue_lines ADD COLUMN unit_cost DOUBLE;
ALTER TABLE goods_issue_lines ADD COLUMN line_total DOUBLE;

