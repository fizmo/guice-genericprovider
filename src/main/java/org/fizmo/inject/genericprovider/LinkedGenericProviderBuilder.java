package org.fizmo.inject.genericprovider;

import com.google.inject.Module;

public interface LinkedGenericProviderBuilder<T>
{
    Module toProvider(GenericProvider provider);
    Module toProvider(Class<? extends GenericProvider> providerType);
}
