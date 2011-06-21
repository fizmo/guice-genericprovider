/**
 * <p>A package for creating generic provider classes.</p>
 *
 * <p>
 * Many libraries provide a form of factory function that can be used to build
 * object instances when given a {@link java.lang.Class} object. But the standard
 * {@link com.google.inject.Provider} interface does not provide any access to
 * the Class object that has been requested. This requires provider writers to
 * create instances of their providers with the Class as a parameter, and any
 * provider dependencies must use field injecton, which may not be the
 * preferred technique for some communities.
 * </p>
 *
 * <p>
 * As a motivating example, consider <a href="http://easymock.org/">EasyMock</a>:
 * </p>
 *
 * <pre>
 * {@code Foo foo = EasyMock.createNiceMock(Foo.class);}
 * </pre>
 *
 * <p>
 * This is a typical factory function of a kind prevelant in many libraries
 * that, when correctly configured, can provide virtually any object given the
 * appropriate Class instance.
 * </p>
 *
 * <p>
 * If one wanted to write a Provider class for this API, a common approach is
 * to write a Provider builder:
 * </p>
 *
 * <pre>
 * {@code
 * public class MockProviderBuilder
 * {
 *     public static <T> Provider<T> build(final Class<T> cls)
 *     {
 *          return new Provider<T>()
 *          {
 *              public T get()
 *              {
 *                  return EasyMock.createNiceMock(cls);
 *              }
 *          };
 *     }
 * }
 * }
 * </pre>
 *
 * <p>
 * In the client {@link com.google.inject.Module}, one would then need to
 * write:
 * </p>
 *
 * <pre>
 * {@code
 * public class MockedModule extends AbstractModule
 * {
 *     public void configure()
 *     {
 *         bind(Foo.class).toProvider(MockProviderBuilder.build(Foo.class));
 *     }
 * }
 * }
 * </pre>
 *
 * <p>
 * The repetition in this declaration is not completely evil, but it is
 * distasteful. The Provider is trival, and in practice would probably
 * be handled by a {@link com.google.inject.Provides} method instead.
 * </p>
 *
 * <p>
 * However, many cases come up where the Provider has dependencies we would
 * like to inject. Since our builder creates object instances outside of
 * Guice, these would need to be injected separately:
 * </p>
 *
 * <pre>
 * {@code
 * public class InjectedModule extends AbstractModule
 * {
 *     public void configure()
 *     {
 *         final Provider<Foo> provider = NeedsInjectionProviderBuilder.build(Foo.class);
 *         requestInjection(provider);
 *         bind(Foo.class).toProvider(provider);
 *     }
 * }
 * }
 * </pre>
 *
 * <p>
 * This example could still be more easily handled by a Provides method, but
 * such methods aren't reusable in different modules, and for complex Providers
 * it becomes desireable to have separate, testable classes.
 * </p>
 *
 * <p>
 * {@link GenericProvider} provides an alternate mechanism to declare a provider
 * as being capable of producing an object of any class requested, provided it
 * is given the Class instance. Subclasses of GenericProvider are then bound
 * using the {@link GenericProviderModuleBuilder} EDSL:
 * </p>
 *
 * <pre>
 * {@code
 * public class GenericProviderModule extends AbstractModule
 * {
 *     public void configure()
 *     {
 *         install(GenericProviderModuleBuilder.bind(Foo.class)
 *                                             .toProvider(MockProvider.class));
 *     }
 * }
 * }
 * </pre>
 *
 * @since 1.0
 */

package org.fizmo.inject.genericprovider;