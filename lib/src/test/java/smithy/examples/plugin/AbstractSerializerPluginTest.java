package smithy.examples.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import software.amazon.smithy.build.MockManifest;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.ModelAssembler;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractSerializerPluginTest {

    protected static Stream<Arguments> testYamlPlugin() {
        return Stream.of(Arguments.arguments(Stream.of(
                AbstractSerializerPluginTest.class.getResource("model.smithy"),
                AbstractSerializerPluginTest.class.getResource("traits.smithy"))
                .collect(Collectors.toSet()),
                AbstractSerializerPluginTest.class
                        .getResource("expectedYaml.yaml")));
    }

    @ParameterizedTest
    @MethodSource("testYamlPlugin")
    public void testExecuteWithConfig(Set<URL> modelList,
                                      URL pathToExpectedYaml)
            throws IOException {
        // setup
        YamlSerializerPlugin yamlSerializerPlugin = new YamlSerializerPlugin();

        ModelAssembler modelAssembler = Model.assembler();
        modelList.forEach(modelAssembler::addImport);
        Model model = modelAssembler.assemble().unwrap();

        MockManifest mockManifest = new MockManifest();

        PluginContext pluginContext = PluginContext.builder()
                .model(model)
                .fileManifest(mockManifest)
                .build();

        // trigger
        yamlSerializerPlugin.executeWithConfig(pluginContext,
                new YamlSerializerPlugin.Config());

        // verify
        assertTrue(
                mockManifest.hasFile(yamlSerializerPlugin.getOutputFileName()));

        JsonNode expected = new ObjectMapper(new YAMLFactory())
                .readTree(pathToExpectedYaml);
        JsonNode actual = new ObjectMapper(new YAMLFactory())
                .readTree(mockManifest.expectFileString(
                        yamlSerializerPlugin.getOutputFileName()));
        assertEquals(expected, actual);
    }

}
