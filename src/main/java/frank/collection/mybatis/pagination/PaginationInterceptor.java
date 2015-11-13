package frank.collection.mybatis.pagination;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import frank.collection.pagination.PageUtil;
import frank.collection.pagination.bean.Page;

/**
 * 为ibatis3提供基于方言(Dialect)的分页查询的插件
 *
 * 将拦截Executor.query()方法实现分页方言的插入.
 *
 * 配置文件内容:
 *
 * <pre>
 * 	&lt;plugins>
 * 	&lt;plugin interceptor="cn.org.rapid_framework.ibatis3.plugin.OffsetLimitInterceptor">
 * 		&lt;property name="dialectClass" value="cn.org.rapid_framework.jdbc.dialect.MySQLDialect"/>
 * 	&lt;/plugin>
 * &lt;/plugins>
 * </pre>
 *
 * @author badqiu
 *
 */

@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PaginationInterceptor implements  Interceptor {
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;
	static int RESULT_HANDLER_INDEX = 3;

	Dialect dialect;

	protected Transaction transaction;

	public Object intercept(Invocation invocation) throws Throwable {
		CachingExecutor ce = (CachingExecutor) invocation.getTarget();
		transaction = ce.getTransaction();
		processIntercept(invocation.getArgs());
		return invocation.proceed();
	}

	/**
	 * 计算游标位置,计算pagepojo信息
	 *
	 * @param pagePojo
	 * @param ms
	 * @param parameter
	 */
	public void handlePage(Page page, MappedStatement ms, Object parameter) {
		int offset = 0;
		if (page.getPageno().intValue() > 1) {
			offset = (int) ((page.getPageno() - 1) * page.getPagerows());
		}
		Configuration cfg = ms.getConfiguration();
		String id = ms.getId();
		Object param = parameter;
		Long rowsAll = PageListHelper.getTotalCount(cfg, transaction, id,page, param);
		Page tempPage = PageUtil.getPage(page.getPageno(), page.getPagerows(), rowsAll);
		page.setRowsall(tempPage.getRowsall());
		page.setPagenoall(tempPage.getPagenoall());
		if(page.getPagenoall()==1){
			offset = 0;
		}
		rowBounds = new RowBounds(offset, page.getPagerows().intValue());
	}

	RowBounds rowBounds;
	
	/**
	 * 3 way you can use for passing the page entity
	 * @param parameter
	 * @return
	 */
	private Page findPageEntity(Object parameter) {
		if (parameter == null)
			return null;
		// case like :getList2(Page page)
		if ((parameter instanceof Page))  
			return (Page) parameter;
		if (parameter instanceof Map) {
			Map map = (Map)parameter ;
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				Object param = map.get(key);
				if (param == null)
					continue;
				if (param instanceof Page) {
					//case like : getList3(Map map); 
					return (Page) param;
				}
				if (param.getClass().isArray()) {
					Object[] array = (Object[]) param;
					for (Object o : array) {
						if (o != null && o instanceof Page) {
							//case like : getList3(Map map);
							return (Page) o;
						}
					}
				}
			}
			
		}
		return null;
	}
	
	private void processIntercept(final Object[] queryArgs) {
		// queryArgs = query(MappedStatement ms, Object parameter, RowBounds
		// rowBounds, ResultHandler resultHandler)

		MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		Object parameter = queryArgs[PARAMETER_INDEX];
		rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
		 Page page = findPageEntity(parameter);
		if(page!=null){
			this.handlePage((page), ms, parameter);	
		}
		int offset = rowBounds.getOffset();
		int limit = rowBounds.getLimit();
		if (dialect.supportsLimit() && (offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT)) {
			BoundSql boundSql = ms.getBoundSql(parameter);
			String sql = boundSql.getSql().trim();
			if (dialect.supportsLimitOffset()) {
				sql = dialect.getLimitString(sql, offset, limit);
				offset = RowBounds.NO_ROW_OFFSET;
			} else {
				sql = dialect.getLimitString(sql, 0, limit);
			}
			limit = RowBounds.NO_ROW_LIMIT;
			queryArgs[ROWBOUNDS_INDEX] = new RowBounds(offset, limit);
			BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
			MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
			queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
		}
	}

	// see: MapperBuilderAssistant
	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		builder.keyProperty(StringUtils.join(ms.getKeyProperties(), ","));
		// ms.getke
		// setStatementTimeout()
		builder.timeout(ms.getTimeout());

		// setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());

		// setStatementResultMap()
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		// setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		String dialectClass = new PropertiesHelper(properties).getRequiredString("dialectClass");
		try {
			dialect = (Dialect) Class.forName(dialectClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("cannot create dialect instance by dialectClass:" + dialectClass, e);
		}
		System.out.println(PaginationInterceptor.class.getSimpleName() + ".dialect=" + dialectClass);
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

}
