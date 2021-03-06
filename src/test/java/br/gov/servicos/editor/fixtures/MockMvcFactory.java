package br.gov.servicos.editor.fixtures;

import br.gov.servicos.editor.conteudo.MockMvcEditorAPI;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class MockMvcFactory {

    public static MockMvc get(WebApplicationContext context) {
        return MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    public static MockMvcEditorAPI editorAPI(WebApplicationContext context) {
        return new MockMvcEditorAPI(get(context));
    }

}
