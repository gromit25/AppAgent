package com.redeye.appagent.builtins.db;

/**
 * DB 수행 종류 코드
 * 
 * @author jmsohn
 */
enum ActionType {

	/** DB 연결(connect) */
	DB_CON,
	/** DB 연결 해제(close) */
	DB_CLS,

	/** sql 수행 - Merge 등등 구분이 어려운 경우 */
	DB_SQL,

	/** select 수행 */
	DB_SEL,
	/** insert, update, delete 수행 */
	DB_CUD,

	/** commit 수행 */
	DB_CMT,
	/** rollback 수행 */
	DB_RBK;
}
