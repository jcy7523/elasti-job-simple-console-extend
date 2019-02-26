/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.elasticjob.lite.console.service.impl;

import io.elasticjob.lite.console.domain.GlobalConfiguration;
import io.elasticjob.lite.console.domain.RegistryCenterConfiguration;
import io.elasticjob.lite.console.domain.RegistryCenterConfigurations;
import io.elasticjob.lite.console.repository.ConfigurationsXmlRepository;
import io.elasticjob.lite.console.repository.impl.ConfigurationsXmlRepositoryImpl;
import io.elasticjob.lite.console.service.RegistryCenterConfigurationService;
import io.elasticjob.lite.lifecycle.domain.User;

import java.util.Set;

import com.google.common.base.Optional;

/**
 * 注册中心配置服务实现类.
 *
 * @author zhangliang
 */
public final class RegistryCenterConfigurationServiceImpl implements RegistryCenterConfigurationService {
    private static final String ROOT_DEFAULT_USERNAME = "root";

    private ConfigurationsXmlRepository configurationsXmlRepository = new ConfigurationsXmlRepositoryImpl();
    
    @Override
    public RegistryCenterConfigurations loadAll(User user) {
    	String privilege = user.getPrivileges();
    	RegistryCenterConfigurations registryCenterConfigurations = loadGlobal().getRegistryCenterConfigurations();
    	Set<RegistryCenterConfiguration> set = registryCenterConfigurations.getRegistryCenterConfiguration();
    	if(ROOT_DEFAULT_USERNAME.equals(user.getUsername())){
    		return registryCenterConfigurations;
    	}
    	for(RegistryCenterConfiguration config : set){
    		if(privilege.indexOf(config.getName()) == -1){
    			set.remove(config);
    		}
    	}
    	return registryCenterConfigurations;
    }
    
    @Override
    public RegistryCenterConfiguration load(final String name) {
        GlobalConfiguration configs = loadGlobal();
        RegistryCenterConfiguration result = find(name, configs.getRegistryCenterConfigurations());
        setActivated(configs, result);
        return result;
    }
    
    @Override
    public RegistryCenterConfiguration find(final String name, final RegistryCenterConfigurations configs) {
        for (RegistryCenterConfiguration each : configs.getRegistryCenterConfiguration()) {
            if (name.equals(each.getName())) {
                return each;
            }
        }
        return null;
    }
    
    private void setActivated(final GlobalConfiguration configs, final RegistryCenterConfiguration toBeConnectedConfig) {
        RegistryCenterConfiguration activatedConfig = findActivatedRegistryCenterConfiguration(configs);
        if (!toBeConnectedConfig.equals(activatedConfig)) {
            if (null != activatedConfig) {
                activatedConfig.setActivated(false);
            }
            toBeConnectedConfig.setActivated(true);
            configurationsXmlRepository.save(configs);
        }
    }
    
    @Override
    public Optional<RegistryCenterConfiguration> loadActivated() {
        return Optional.fromNullable(findActivatedRegistryCenterConfiguration(loadGlobal()));
    }
    
    private RegistryCenterConfiguration findActivatedRegistryCenterConfiguration(final GlobalConfiguration configs) {
        for (RegistryCenterConfiguration each : configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration()) {
            if (each.isActivated()) {
                return each;
            }
        }
        return null;
    }
    
    @Override
    public boolean add(final RegistryCenterConfiguration config) {
        GlobalConfiguration configs = loadGlobal();
        boolean result = configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration().add(config);
        if (result) {
            configurationsXmlRepository.save(configs);
        }
        return result;
    }
    
    @Override
    public void delete(final String name) {
        GlobalConfiguration configs = loadGlobal();
        RegistryCenterConfiguration toBeRemovedConfig = find(name, configs.getRegistryCenterConfigurations());
        if (null != toBeRemovedConfig) {
            configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration().remove(toBeRemovedConfig);
            configurationsXmlRepository.save(configs);
        }
    }
    
    private GlobalConfiguration loadGlobal() {
        GlobalConfiguration result = configurationsXmlRepository.load();
        if (null == result.getRegistryCenterConfigurations()) {
            result.setRegistryCenterConfigurations(new RegistryCenterConfigurations());
        }
        return result;
    }
}
