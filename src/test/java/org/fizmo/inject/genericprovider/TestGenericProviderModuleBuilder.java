/**
 * Copyright (C) 2011 Christopher Currie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fizmo.inject.genericprovider;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TestGenericProviderModuleBuilder
{
    interface Foo
    {
        public boolean getValue();
    }

    static class MockProvider implements GenericProvider
    {
        public <T> T get(Class<T> cls)
        {
            return createNiceMock(cls);
        }
    }

    static class MockModule extends AbstractModule
    {

        @Override
        protected void configure()
        {
            install(GenericProviderModuleBuilder.bind(Foo.class).toProvider(MockProvider.class));
        }
    }

    static class ProviderInstanceMockModule extends AbstractModule
    {

        @Override
        protected void configure()
        {
            install(GenericProviderModuleBuilder.bind(Foo.class).toProvider(new MockProvider()));
        }
    }

    @Test
    public void testProviderModule()
    {
        Injector injector = Guice.createInjector(new MockModule());

        Foo foo = injector.getInstance(Foo.class);

        assertNotNull(foo);
        assertFalse(foo.getValue());
    }

    @Test
    public void testProviderInstance()
    {
        Injector injector = Guice.createInjector(new ProviderInstanceMockModule());

        Foo foo = injector.getInstance(Foo.class);

        assertNotNull(foo);
        assertFalse(foo.getValue());
    }

    @Test
    public void testMockBehavior()
    {
        Injector injector = Guice.createInjector(new MockModule());

        Foo foo = injector.getInstance(Foo.class);
        assertNotNull(foo);

        expect(foo.getValue()).andReturn(true);
        replay(foo);
        assertTrue(foo.getValue());
    }
}

