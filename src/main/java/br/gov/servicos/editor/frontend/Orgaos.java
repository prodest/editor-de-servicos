package br.gov.servicos.editor.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.slugify.Slugify;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static br.gov.servicos.editor.frontend.Siorg.Unidade;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static net.logstash.logback.marker.Markers.append;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class Orgaos implements InitializingBean {

    public static final ClassPathResource RESOURCE = new ClassPathResource("estrutura-organizacional.json.gz");

    RestTemplate rest;
    ObjectReader reader;
    Slugify slugify;

    @NonFinal
    EstruturaOrganizacional estruturaOrganizacional;

    @Autowired
    public Orgaos(@Qualifier("restTemplate") RestTemplate rest, ObjectMapper mapper, Slugify slugify) {
        this.rest = rest;
        this.slugify = slugify;
        reader = mapper.reader(EstruturaOrganizacional.class);
    }

    @Override
    @SneakyThrows
    public void afterPropertiesSet() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        OrgaoES[] orgaos = restTemplate.getForObject("https://api.es.gov.br/organograma", OrgaoES[].class);
        log.debug("Lendo estrutura organizacional de {}...", RESOURCE);
        estruturaOrganizacional = new EstruturaOrganizacional(convertToUnidade(orgaos));
        log.debug("Estrutura organizacional lida com sucesso", RESOURCE);
    }

    public List<Unidade> convertToUnidade (OrgaoES[] orgaos) {
        List<Unidade> list = Arrays.stream(orgaos)
                .map(u -> new Unidade("https://api.es.gov.br/organograma/" + u.getId().toString(),u.getRazaoSocial(),u.getSigla()))
                .collect(toList());
        return list;
    }

    @Cacheable("orgaos")
    public List<OrgaoDTO> get(String termo) {
        List<OrgaoDTO> busca = estruturaOrganizacional.getUnidades()
                .stream()
                .filter(new FiltroDeOrgaos(termo))
                .map(u -> new OrgaoDTO().withNome(String.format("%s (%s)", u.getNome().trim(), u.getSigla().trim()))
                        .withId(u.getCodigoUnidade()))
                .sorted(comparing(OrgaoDTO::getNome))
                .collect(toList());

        log.info(append("orgaos.termo", termo).and(append("orgaos.resultados", busca.size())),
                "Buscando órgãos com termo '{}': {} resultados", termo, busca.size());

        return busca;
    }

    @Data
    @Wither
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstruturaOrganizacional {
        List<Unidade> unidades;
    }


    @FieldDefaults(level = PRIVATE, makeFinal = true)
    private class FiltroDeOrgaos implements Predicate<Unidade> {

        String termo;

        public FiltroDeOrgaos(String termo) {
            this.termo = termo;
        }

        @Override
        public boolean test(Unidade u) {
            return limpa(u.getNome()).contains(limpa(termo)) || limpa(u.getSigla()).contains(limpa(termo));
        }

        private String limpa(String s) {
            return slugify.slugify(s).replace('-', ' ');
        }

    }


    @Data
    @Wither
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrgaoES {
        public Integer Id;
        public Integer IdTipoParte;
        public Integer IdTipoRelacao;
        public String RazaoSocial;
        public String Sigla;
        public Integer IdAtividade;
        public Integer IdNaturezaJuridica;
        public Boolean Ativo;
        public Boolean AceitaBoletimEletronico;
        public Integer Total;
    }




}
