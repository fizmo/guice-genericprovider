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
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;

import javax.inject.Inject;

/**
 * <p>A Module builder to enable generic provider classes.</p>
 *
 * <p>Usage:</p>
 *
 * <pre>
 * {@code
 * public class GenericProviderModule extends AbstractModule
 * {
 *     public void configure()
 *     {
 *         install(GenericProviderModuleBuilder.bind(Foo.class)
 *                                             .toProvider(MyGenericProvider.class));
 *     }
 * }
 * }
 * </pre>

 */
public class GenericProviderModuleBuilder
{
    public static <T> LinkedGenericProviderBuilder<T> bind(final Class<T> cls)
    {
        return new ProviderBuilder<T>(cls);
    }

    private static class ProviderBuilder<T> implements LinkedGenericProviderBuilder<T>
    {
        private final Class<T> cls;

        private ProviderBuilder(final Class<T> cls)
        {
            this.cls = cls;
        }

        public Module toProvider(final GenericProvider provider)
        {
            return new AbstractModule()
            {
                @Override
                protected void configure()
                {
                    bind(cls).toProvider(new Provider<T>()
                    {
                        public T get()
                        {
                            return provider.get(cls);
                        }
                    });
                }
            };
        }

        public Module toProvider(final Class<? extends GenericProvider> providerType)
        {
            return new PrivateModule()
            {
                @Override
                protected void configure()
                {
                    bind(GenericProvider.class).to(providerType);
                    final Provider<T> provider = new GenericProviderWrapper<T>(cls, providerType);
                    bind(cls).toProvider(provider);
                    expose(cls);
                }
            };
        }
    }

    private static class GenericProviderWrapper<T> implements Provider<T>
    {
        private final Class<T> cls;
        private final Class<? extends GenericProvider> providerType;

        public GenericProviderWrapper(final Class<T> cls, final Class<? extends GenericProvider> providerType)
        {
            this.cls = cls;
            this.providerType = providerType;
        }

        @Inject private Injector injector;

        public T get()
        {
            final GenericProvider provider = injector.getInstance(providerType);
            return provider.get(cls);
        }
    }

}
