package com.zl;

import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ConfigTest
 * @Description: 单元测试
 * @Author: zl
 * @Date: 2019/10/10 21:57
 * @Version: 1.0
 **/
public class ConfigTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);

    @Test
    public void testConfig1() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        logger.info("processEngineCfg:{}", cfg);
    }

    @Test
    public void testConfig2(){
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        logger.info("processEngineCfg:{}", cfg);
    }
}
