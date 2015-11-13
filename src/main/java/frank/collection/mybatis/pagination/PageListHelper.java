package frank.collection.mybatis.pagination;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandlerRegistry;

import frank.collection.mybatis.pagination.PaginationInterceptor.BoundSqlSqlSource;
import frank.collection.pagination.bean.Page;

public class PageListHelper {
	
	/**
	 * 获取总记录数,如果不正常,则抛出异常或者返回-1
	 * @param config
	 * @param transaction
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public static Long getTotalCount(Configuration config, Transaction transaction, String statement,Page page, Object parameter) {
		try { 
			//如果page对象设置了缓存记录总数的功能,直接使用已有的rowsall,注意前台不要在回传的时候丢失rowsall
			if (page!=null&&page.isCountCache()&&page.getRowsall()>0) {
				return page.getRowsall();
			} else {
			//如果没有,则查询记录总数
				return  getCountFromDB(config, transaction, statement, parameter);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return -1l;
	}
	
	/** 查询总记录数 **/
	private static Long getCountFromDB (Configuration config, Transaction transaction, String statement, Object parameter) throws SQLException {
		MappedStatement ms = config.getMappedStatement(statement);
		BoundSql boundSql = ms.getBoundSql(parameter);
		String sql = boundSql.getSql().trim();
		sql = "select count(1) as totalCount from (" + sql + ") A";
		BoundSql newBoundSql = new BoundSql(config, sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
		MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
		Executor executor = new SimpleExecutor(config, transaction);
		List totalCountList = executor.query(newMs, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
		Long count = Long.parseLong(totalCountList.get(0).toString());
		return count ;
	}
	
	private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
		
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		builder.keyProperty(StringUtils.join(ms.getKeyProperties(), ","));

		// setStatementTimeout()
		builder.timeout(ms.getTimeout());

		// setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());

		// setStatementResultMap()
		// builder.resultMaps(ms.getResultMaps());
		builder.resultMaps(getPageResultMaps(ms.getConfiguration()));
		builder.resultSetType(ms.getResultSetType());

		// setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	private static List<ResultMap> getPageResultMaps(final Configuration config) {
		final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
		List<ResultMap> resultMaps = new ArrayList<ResultMap>() {
			{
				add(new ResultMap.Builder(config, "defaultResultMap", int.class, new ArrayList<ResultMapping>() {
					{
						add(new ResultMapping.Builder(config, "totalCount", "totalCount", registry.getTypeHandler(int.class)).build());
					}
				}).build());
			}
		}; 
		return resultMaps;
	}
}
