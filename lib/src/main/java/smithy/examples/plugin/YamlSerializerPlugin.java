package smithy.examples.plugin;

import com.google.auto.service.AutoService;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.Prelude;
import software.amazon.smithy.model.shapes.Shape;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class serializes the Smithy model into a YAML format.
 *
 * Example usage:
 *
 * <pre>
 * "plugins": {
 *     "yaml": {}
 *}
 * </pre>
 */
@AutoService(SmithyBuildPlugin.class)
public final class YamlSerializerPlugin
        extends AbstractSerializerPlugin<YamlSerializerPlugin.Config> {

    private static final String PATH_TO_YAML_TEMPLATE = "templates/yaml.vm";

    private final VelocityEngine velocityEngine;
    private final VelocityContext velocityContext;
    private final Writer writer;

    public YamlSerializerPlugin() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,
                "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        velocityContext = new VelocityContext();

        writer = new StringWriter();
    }

    @Override
    public String getName() {
        return "yaml";
    }

    @Override
    public Class<Config> getConfigType() {
        return Config.class;
    }

    @Override
    protected String getOutputFileName() {
        return "model.yaml";
    }

    @Override
    protected String serializeModel(Model model) {
        velocityEngine.getTemplate(PATH_TO_YAML_TEMPLATE)
                .merge(buildContext(model), writer);

        return writer.toString();
    }

    private VelocityContext buildContext(Model model) {
        Collection<Shape> filteredModels = model.shapes()
                // to match default build artifact (which does not have Prelude
                // shapes or MemberShapes)
                .filter(shape -> !shape.getId()
                        .getNamespace()
                        .equals(Prelude.NAMESPACE))
                .filter(shape -> !shape.isMemberShape())
                // sort shapes by ShapeId to match default build artifact and
                // for deterministic outputs
                .sorted()
                .collect(Collectors.toList());

        velocityContext.put("shapes", filteredModels);
        return velocityContext;
    }

    /**
     * This class represents the configuration passed to the plugin in
     * smithy-build.json, which is deserialized into this class.
     */
    public static final class Config extends AbstractSerializerPlugin.Config {
    }

}
