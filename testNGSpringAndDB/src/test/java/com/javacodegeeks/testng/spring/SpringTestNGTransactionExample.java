package com.javacodegeeks.testng.spring;

import static org.springframework.test.context.transaction.TestTransaction.end;
import static org.springframework.test.context.transaction.TestTransaction.flagForCommit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {ImportResourceValueProperties.class})
public class SpringTestNGTransactionExample extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String method;

    @BeforeMethod
    public void saveMethodName(Method method) {
        this.method = method.getName();
    }

    @Test
    public void tran() {
        assertNotNull(jdbcTemplate);
    }

    @BeforeTransaction
    public void beforeTransaction() {
        deleteFromTables("public.table");
        executeSqlScript("classpath:data.sql", false);
    }

    @Test
    public void insertEmployeeAndCommit() {
        String emp = "Bill";
        jdbcTemplate.update("insert into public.table (name) values (?)", emp);
        assertEquals(countRowsInTableWhere("public.table", "name='" + emp + "'"), 1);
        flagForCommit();
        end();
    }

    @Test
    public void insertEmployeeWithRollbackAsDefault() {
        String emp = "Bill";
        jdbcTemplate.update("insert into public.table (name) values (?)", emp);
        List<String> actual = jdbcTemplate.queryForList("select name from public.table", String.class);
        assertEquals(countRowsInTableWhere("public.table", "name='" + emp + "'"), 1);
    }

    @Test
    @Rollback(false)
    public void insertEmployeeWithCommitAsDefault() {
        String emp = "Bill";
        jdbcTemplate.update("insert into public.table (name) values (?)", emp);
        assertEquals(countRowsInTableWhere("public.table", "name='" + emp + "'"), 1);
    }

    @Test
    @Sql({"/additional_data.sql"})
    public void insertEmployeeUsingSqlAnnotation() {
        assertEquals(countRowsInTableWhere("public.table", "name='dd'"), 1);
    }

    @AfterTransaction
    public void afterTransaction() {
        switch (method) {
            case "insertEmployeeAndCommit":
                assertEmployees("");
                break;
            case "insertEmployeeWithRollbackAsDefault":
                assertEmployees("");
                break;
            case "insertEmployeeWithCommitAsDefault":
                assertEmployees("");
                break;
            case "tran":
                break;
            case "insertEmployeeUsingSqlAnnotation":
                assertEmployees("");
                break;
            default:
                throw new RuntimeException("missing 'after transaction' assertion for test method: " + method);
        }
    }

    private void assertEmployees(String... users) {
        List<String> actual = jdbcTemplate.queryForList("select name from public.table", String.class);
        List<Map<String, Object>> actual2 = jdbcTemplate.queryForList("select * from public.table");
        Collections.sort(actual);
    }
}