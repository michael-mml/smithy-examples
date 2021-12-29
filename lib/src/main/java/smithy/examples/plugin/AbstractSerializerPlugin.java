package smithy.examples.plugin;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.plugins.ConfigurableSmithyBuildPlugin;
import software.amazon.smithy.model.Model;

/**
 * This class represents the base class that serializes the Smithy model into
 * some format.
 */
public abstract class AbstractSerializerPlugin<T extends AbstractSerializerPlugin.Config>
        extends ConfigurableSmithyBuildPlugin<T> {

    /**
     * Gets the name of the output artifact.
     *
     * @return the name of the output artifact
     */
    protected abstract String getOutputFileName();

    /**
     * Serializes the Smithy model into a string.
     *
     * @param model the Smithy model
     * @return the serialized model
     */
    protected abstract String serializeModel(Model model) throws Exception;

    @Override
    protected void executeWithConfig(PluginContext context, Config config) {
        try {
            context.getFileManifest()
                    .writeFile(getOutputFileName(),
                            serializeModel(context.getModel()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This abstract class represents the configuration passed to the plugin in
     * smithy-build.json, which is deserialized into a concrete implementation
     * of {@link Config}.
     *
     * Concrete implementations of {@link AbstractSerializerPlugin} must define
     * a static nested class that extends {@link Config} and declare members in
     * that class that match the key-value pairs passed to the plugin
     * configuration.
     *
     * See {@link YamlSerializerPlugin} for an example. See
     * {@link ConfigurableSmithyBuildPlugin#getConfigType()} for more details.
     */
    public abstract static class Config {

    }

}
