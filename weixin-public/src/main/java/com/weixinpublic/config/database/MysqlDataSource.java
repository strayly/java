package com.weixinpublic.config.database;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.weixinpublic.config.mybatis.TimeMetaObjectHandler;
import com.weixinpublic.enums.ErrorEnum;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;



@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.weixinpublic.mapper.mysql"}, sqlSessionTemplateRef = "commonMysqlSqlSessionTemplate")
@Slf4j
public class MysqlDataSource {
    @Value("${spring.datasource.mySqlPassWord}")
    protected String mySqlPassWord;

    @Bean(name = "dataSourceProperties")
    @ConfigurationProperties(prefix ="spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        //密文
        dataSourceProperties.setPassword("");

        return dataSourceProperties;
    }

    @Bean(name = "commonMysqlDataSource")
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource dataSource (@Qualifier("dataSourceProperties") DataSourceProperties mysqlDataSourceProperties) {

        return mysqlDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "commonMysqlSqlSessionFactory")
    public SqlSessionFactory entityManagerFactory(@Qualifier("commonMysqlDataSource") DataSource dataSource,
                                                  @Qualifier("timeMetaObjectHandler") TimeMetaObjectHandler timeMetaObjectHandler,
                                                  @Qualifier("mybatisPlusInterceptor") MybatisPlusInterceptor mybatisPlusInterceptor) {

        try {
            MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
            bean.setGlobalConfig(new GlobalConfig().setMetaObjectHandler(timeMetaObjectHandler));
            bean.setDataSource(dataSource);
            bean.setPlugins(mybatisPlusInterceptor);
            return bean.getObject();
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ErrorEnum.MYSQL_SESSION_FACTORY_INITIALIZE_ERROR, "commonMysqlSqlSessionFactory初始化异常", e);
        }
    }

    @Bean(name = "commonMysqlTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("commonMysqlDataSource") DataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "commonMysqlSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("commonMysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {

        return new SqlSessionTemplate(sqlSessionFactory);
    }


}


