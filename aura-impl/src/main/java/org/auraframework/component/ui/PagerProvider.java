/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.component.ui;

import javax.inject.Inject;

import org.auraframework.annotations.Annotations.ServiceComponentProvider;
import org.auraframework.def.ComponentDef;
import org.auraframework.def.ComponentDescriptorProvider;
import org.auraframework.def.DefDescriptor;
import org.auraframework.instance.BaseComponent;
import org.auraframework.service.ContextService;
import org.auraframework.service.DefinitionService;
import org.auraframework.throwable.AuraRuntimeException;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

/**
 * Determine which type of pager implementation to render, based on type
 * attribute
 * 
 * Currently supports 'JumpToPage', 'NextPrevious' (default), 'PageInfo',
 * 'PageSize', or a namespaced component, eg. ns:CustomPager
 */
@ServiceComponentProvider
public class PagerProvider implements ComponentDescriptorProvider {
	@Inject
	ContextService contextService;
	
	@Inject
	DefinitionService definitionService;
	
    // attribute name
    private static final String TYPE_ATTRIBUTE = "type";

    // permissible types
    private static final ImmutableMap<String, String> typeMap = new ImmutableMap.Builder<String, String>()
            .put("JumpToPage", "ui:pagerJumpToPage").put("NextPrevious", "ui:pagerNextPrevious")
            .put("PageInfo", "ui:pagerPageInfo").put("PageSize", "ui:pagerPageSize").put("", "ui:pagerNextPrevious")
            .build();

    @Override
    public DefDescriptor<ComponentDef> provide() throws QuickFixException {
        BaseComponent<?, ?> component = contextService.getCurrentContext().getCurrentComponent();
        String type = (String) component.getAttributes().getValue(TYPE_ATTRIBUTE);
        if (type == null) {
            type = "";
        }
        String typeDescriptor = type.contains(":") ? type : typeMap.get(type);
        if (typeDescriptor != null) {
            return definitionService.getDefDescriptor(typeDescriptor, ComponentDef.class);
        }
        throw new AuraRuntimeException("Unknown type attribute specified for ui:pager '" + type
                + "'. Remove the type attribute or use one of the following values: '"
                + Joiner.on("', '").join(typeMap.keySet())
                + "', or any namespaced component descriptor, e.g. ns:CustomPager.");
    }
}
