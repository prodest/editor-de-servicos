'use strict';

module.exports = {

  controller: function (args) {
    this.servico = args.servico;

    this.adicionar = function () {
      this.servico().legislacoes().push('');
    };

    this.remover = function (i) {
      this.servico().legislacoes().splice(i, 1);
    };
  },

  view: function (ctrl) {
    return m('fieldset#legislacoes', [
      m('h3', [
        'Legislações relacionadas ao serviço',
        m.component(require('tooltips').legislacoes)
      ]),

      ctrl.servico().legislacoes().map(function (legislacao, i) {
        return [
          m('input.inline[type=text]', {
            value: legislacao,
            onchange: function (e) {
              ctrl.servico().legislacoes()[i] = e.target.value;
            }
          }),
          m('button.remove', {
            onclick: ctrl.remover.bind(ctrl, i)
          }, [
            m('span.fa.fa-trash-o')
          ])
        ];
      }),
      m('button.adicionar.adicionar-legislacao', {
        onclick: ctrl.adicionar.bind(ctrl)
      }, [
        m('i.fa.fa-plus'),
        ' Adicionar lei, decreto ou portaria '
      ])
    ]);
  }
};