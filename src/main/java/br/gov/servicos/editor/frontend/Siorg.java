package br.gov.servicos.editor.frontend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class Siorg {

    public static final Predicate<String> URL_PREDICATE = Pattern.compile("https://api\\.es\\.gov\\.br/organograma/\\d+").asPredicate();

    RestTemplate restTemplate;

    @Autowired
    public Siorg(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "orgaoSiorg", unless = "#result.isPresent()")
    public Optional<String> nomeDoOrgao(String urlOrgao) {
        if (URL_PREDICATE.negate().test(urlOrgao)) {
            log.warn("URL {} não é uma URL para órgão no Siorg", urlOrgao);
            return empty();
        }

        try {
            Orgaos.OrgaoES entity = restTemplate.getForObject(urlOrgao, Orgaos.OrgaoES.class);

            return ofNullable(String.format("%s (%s)", entity.getRazaoSocial(), entity.getSigla()));

        } catch (RestClientException e) {
            log.warn("Erro ao acessar Siorg", e);
            return empty();
        }
    }

    @Data
    @Wither
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultaUnidadeResumida {
        Servico servico;
        Unidade unidade;
    }

    @Data
    @Wither
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Servico {
        long codigoErro;
        String mensagem;
    }

    @Data
    @Wither
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Unidade {
        String codigoUnidade;
        String nome;
        String sigla;
    }




}

