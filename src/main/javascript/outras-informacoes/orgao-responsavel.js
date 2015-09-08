'use strict';

var erro = require('utils/erro-ajax');

module.exports = {

  controller: function (args) {
    this.servico = args.servico;
  },

  view: function (ctrl) {
    return m('fieldset#orgao-responsavel', [
      m('h2', 'outras informações'),
      m('h3', [
        'Órgão responsável',
        m.component(require('tooltips').orgaoResponsavel)
      ]),

      m('.input-container', [
        m.component(require('componentes/select2'), {
          ajax: {
            url: '/editar/api/orgaos',
            dataType: 'json',
            delay: 250,
            data: function (params) {
              return {
                q: params.term
              };
            },
            processResults: function (data, page) {
              var result = _.map(data, function (o) {
                return {
                  id: o.id,
                  text: o.nome
                };
              });
              return {
                results: result
              };
            },
            cache: true
          },
          prop: ctrl.servico().orgao,
          width: '100%',
          minimumResultsForSearch: 1,
          minimumInputLength: 3,
          initSelection: function (element, callback) {
            m.request({
              method: 'GET',
              url: '/editar/api/orgao',
              data: {
                urlOrgao: ctrl.servico().orgao()
              },
              deserialize: function (data) {
                return data;
              }
            }).then(function (orgao) {
              callback({
                id: ctrl.servico().orgao(),
                text: orgao
              });
            }, erro);

          }
        })
      ])
    ]);
  }
};
