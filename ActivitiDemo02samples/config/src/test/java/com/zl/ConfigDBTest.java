package com.zl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ConfigDBTest
 * @Description: TODO
 * @Author: zl
 * @Date: 2019/10/13 14:12
 * @Version: 1.0
 **/
public class ConfigDBTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigDBTest.class);

    @Test
    public void testConfig1() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        logger.info("configuration = {}", cfg);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        logger.info("获取流程引擎= {}", processEngine.getName());
        processEngine.close();
    }

    @Test
    public void testConfig2() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti_druid.cfg.xml");
        logger.info("configuration = {}", cfg);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        logger.info("获取流程引擎= {}", processEngine.getName());
        processEngine.close();
    }
}
