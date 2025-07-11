package io.github.tml.mosaic.core.world.config;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DynamicBeanNameModifier implements BeanDefinitionRegistryPostProcessor {

    public Map<Class<?>, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // 初始改名
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();
        for (Class<?> clazz : componentClasses){
            String oldBeanName = StringUtil.getFirstLowerCase(clazz.getSimpleName());
            BeanDefinition beanDefinition = registry.getBeanDefinition(oldBeanName);
            BeanDefinition beanDefinitionCopy = new GenericBeanDefinition(beanDefinition);

            beanDefinition.setPrimary(true);

            // 保存现有的BeanDefinition，以便后续创建新的Bean实例
            beanDefinitionMap.put(clazz, beanDefinitionCopy);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        if(beanDefinitionMap.containsKey(clazz)){
            return beanDefinitionMap.get(clazz);
        }
        throw new RuntimeException("beanDefinition not found");
    }
}