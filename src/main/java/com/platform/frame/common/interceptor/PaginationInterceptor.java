package com.platform.frame.common.interceptor;

import com.platform.frame.common.bean.PageBean;
import com.platform.frame.common.dao.dialect.DialectAbstract;
import com.platform.frame.common.dao.dialect.MysqlDialect;
import com.platform.frame.common.tools.Reflections;
import com.platform.frame.common.util.CommonConstants;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class }))
public class PaginationInterceptor implements Interceptor {
    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    // 分页的id后缀
    private static final String SUFFIX_PAGE = "_PageHelper";
    // count查询的id后缀
    private static final String SUFFIX_COUNT = SUFFIX_PAGE + "_Count";
    // 第一个分页参数
    private static final String PAGEPARAMETER_FIRST = "startRow";
    // 第二个分页参数
    private static final String PAGEPARAMETER_SECOND = "pageSize";

    private static final String BOUND_SQL = "boundSql.sql";
    private static final String SQL_NODES = "sqlSource.rootSqlNode.contents";

    private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);

    private static final DialectAbstract dialect = new MysqlDialect();

    /**
     * 反射对象，增加对低版本Mybatis的支持
     *
     * @param object 反射对象
     * @return
     */
    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
    }

    /**
     * Mybatis拦截器方法
     *
     * @param invocation 拦截器入参
     * @return 返回执行结果
     * @throws Throwable 抛出异常
     */
    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        final Object[] args = invocation.getArgs();
        RowBounds rowBounds = (RowBounds) args[2];
        Object parameterObject = args[1];
        if (parameterObject == null || !(parameterObject instanceof PageBean)
                || (!((PageBean) parameterObject).isPagerFlag())) {
            return invocation.proceed();
        }
        if (((PageBean) parameterObject).isPagerFlag()) {
            if (((PageBean) parameterObject).getLimit() == null) {
                ((PageBean) parameterObject).setLimit(CommonConstants.DEFAULT_LIMIT);
            }
            if (((PageBean) parameterObject).getOffset() == null) {
                ((PageBean) parameterObject).setOffset(CommonConstants.DEFAULT_OFFSET);
            }
        }
        // 忽略RowBounds-否则会进行Mybatis自带的内存分页
        args[2] = RowBounds.DEFAULT;
        MappedStatement ms = (MappedStatement) args[0];
        PageBean pageBean = (PageBean) parameterObject;
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        // 将参数中的MappedStatement替换为新的qs
        args[0] = getMappedStatement(ms, boundSql, SUFFIX_COUNT);
        // 查询总数
        Object result = invocation.proceed();

        // 设置总数
        if (result != null) {
            pageBean.setTotal((Integer) ((List) result).get(0));
        }

        if (pageBean.getLimit() > 0
                && ((rowBounds == RowBounds.DEFAULT && pageBean.getLimit() > 0) || rowBounds != RowBounds.DEFAULT)) {
            // BoundSql boundSqlPage = ms.getBoundSql(parameterObject);
            // 将参数中的MappedStatement替换为新的qs
            args[0] = getMappedStatement(ms, boundSql, SUFFIX_PAGE);
        }
        // 返回结果
        return invocation.proceed();
    }

    /**
     * 处理参数对象，添加分页参数值
     *
     * @param parameterObject 参数对象
     * @param PageBean 分页信息
     * @return 返回带有分页信息的参数对象
     */
    private Map setPageParameter(Object parameterObject, BoundSql boundSql, PageBean pageBean) {
        Map paramMap = new HashMap();
        if (parameterObject == null) {
            paramMap = new HashMap();
        } else if (parameterObject instanceof Map) {
            paramMap = (Map) parameterObject;
        } else {
            paramMap = new HashMap();
            if (boundSql.getParameterMappings() != null && boundSql.getParameterMappings().size() > 0) {
                for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
                    if (!parameterMapping.getProperty().equals(PAGEPARAMETER_FIRST)
                            && !parameterMapping.getProperty().equals(PAGEPARAMETER_SECOND)) {
                        boolean hasSameField = false;
                        Field[] declaredFields = parameterObject.getClass().getDeclaredFields();
                        if (declaredFields != null && declaredFields.length > 0) {
                            for (Field field : declaredFields) {
                                String fieldName = field.getName();
                                if (fieldName.equals(parameterMapping.getProperty())) {
                                    hasSameField = true;
                                    break;
                                }
                            }
                        }
                        if (hasSameField) {
                            Object paramValue = Reflections.invokeGetter(parameterObject,
                                    parameterMapping.getProperty());
                            paramMap.put(parameterMapping.getProperty(), paramValue);
                        } else {
                            paramMap.put(parameterMapping.getProperty(), parameterObject);
                        }
                    }
                }
            }
        }
        paramMap.put(PAGEPARAMETER_FIRST, pageBean.getOffset());
        paramMap.put(PAGEPARAMETER_SECOND, pageBean.getLimit());
        return paramMap;
    }

    /**
     * 获取ms - 在这里对新建的ms做了缓存，第一次新增，后面都会使用缓存值
     *
     * @param ms
     * @param boundSql
     * @param suffix
     * @return
     */
    private MappedStatement getMappedStatement(MappedStatement ms, BoundSql boundSql, String suffix) {
        MappedStatement qs = null;
        try {
            qs = ms.getConfiguration().getMappedStatement(ms.getId() + suffix);
        } catch (Exception e) {
            // ignore
        }
        if (qs == null) {
            // 创建一个新的MappedStatement
            qs = createMappedStatement(ms, getNewSqlSource(ms, new BoundSqlSqlSource(boundSql), suffix), suffix);
            try {
                ms.getConfiguration().addMappedStatement(qs);
            } catch (Exception e) {
                // ignore
            }
        }
        return qs;
    }

    /**
     * 自定义简单SqlSource
     */
    private class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }

        public BoundSql getBoundSql() {
            return boundSql;
        }
    }

    /**
     * 自定义动态SqlSource
     */
    public class MyDynamicSqlSource implements SqlSource {
        private Configuration configuration;
        private SqlNode rootSqlNode;

        public MyDynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
            this.configuration = configuration;
            this.rootSqlNode = rootSqlNode;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            DynamicContext context = new DynamicContext(configuration, parameterObject);
            rootSqlNode.apply(context);
            SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
            Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
            SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
            BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
            for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
                boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
            }
            // 添加参数映射
            MetaObject boundSqlObject = forObject(boundSql);
            List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>();
            newParameterMappings.addAll(boundSql.getParameterMappings());
            newParameterMappings.add(new ParameterMapping.Builder(configuration, PAGEPARAMETER_FIRST, Integer.class)
                    .build());
            newParameterMappings.add(new ParameterMapping.Builder(configuration, PAGEPARAMETER_SECOND, Integer.class)
                    .build());
            boundSqlObject.setValue("parameterMappings", newParameterMappings);

            return boundSql;
        }
    }

    /**
     * 新建count查询和分页查询的MappedStatement
     *
     * @param ms
     * @param newSqlSource
     * @param suffix
     * @return
     */
    private MappedStatement createMappedStatement(MappedStatement ms, SqlSource newSqlSource, String suffix) {
        String id = ms.getId() + suffix;
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), id, newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        if (suffix == SUFFIX_PAGE) {
            builder.resultMaps(ms.getResultMaps());
        } else {
            // count查询返回值int
            List<ResultMap> resultMaps = new ArrayList<ResultMap>();
            ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), id, int.class, EMPTY_RESULTMAPPING)
                    .build();
            resultMaps.add(resultMap);
            builder.resultMaps(resultMaps);
        }
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 获取新的sqlSource
     *
     * @param ms
     * @param newSqlSource
     * @param suffix
     * @return
     */
    private SqlSource getNewSqlSource(MappedStatement ms, BoundSqlSqlSource newSqlSource, String suffix) {
        SqlSource sqlSource = ms.getSqlSource();
        // 从XMLLanguageDriver.java和XMLScriptBuilder.java可以看出只有两种SqlSource
        if (sqlSource instanceof DynamicSqlSource) {
            MetaObject msObject = forObject(ms);
            List<SqlNode> contents = (List<SqlNode>) msObject.getValue(SQL_NODES);
            List<SqlNode> newSqlNodes = new ArrayList<SqlNode>(contents.size() + 2);
            // 这里用的等号
            if (suffix == SUFFIX_PAGE) {
                newSqlNodes.add(new TextSqlNode(dialect.getPageSqlBefore()));
                newSqlNodes.addAll(contents);
                newSqlNodes.add(new TextSqlNode(dialect.getPageSqlAfter()));
                return new MyDynamicSqlSource(ms.getConfiguration(), new MixedSqlNode(newSqlNodes));
            } else {
                newSqlNodes.add(new TextSqlNode(dialect.getCountSqlBefore()));
                newSqlNodes.addAll(contents);
                newSqlNodes.add(new TextSqlNode(dialect.getCountSqlAfter()));
                return new DynamicSqlSource(ms.getConfiguration(), new MixedSqlNode(newSqlNodes));
            }
        } else {
            // RawSqlSource
            // 这里用的等号
            if (suffix == SUFFIX_PAGE) {
                // 改为分页sql
                MetaObject sqlObject = forObject(newSqlSource);
                sqlObject.setValue(BOUND_SQL, dialect.getPageSql((String) sqlObject.getValue(BOUND_SQL)));
                // 添加参数映射
                List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>();
                newParameterMappings.addAll(newSqlSource.getBoundSql().getParameterMappings());
                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_FIRST,
                        Integer.class).build());
                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND,
                        Integer.class).build());
                sqlObject.setValue("boundSql.parameterMappings", newParameterMappings);
                return newSqlSource;
            } else {
                // 改为count sql
                return new RawSqlSource(ms.getConfiguration(),dialect.getCountSql((String) newSqlSource.getBoundSql().getSql()),null);
            }
        }
    }

    /**
     * 只拦截Executor
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * 设置属性值
     *
     * @param p 属性值
     */
    public void setProperties(Properties p) {
    }
}