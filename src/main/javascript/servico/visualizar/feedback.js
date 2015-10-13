'use strict';

module.exports = {

  controller: function (args) {},

  view: function () {
    return m('form.feedback-servico', [
        m('h3.opiniao', 'Você achou as informações acima úteis?'),
        m('p.formulario-opiniao', [
            m('label', {
          for: 'sim'
        }, 'Sim'),
            m('input[type=radio]#sim.left', {
          name: 'conteudoEncontrado',
          value: 'true',
          required: 'required'
        }),
            m('br.clear'),
            m('label', {
          for: 'nao'
        }, 'Não'),
            m('input[type=radio]#nao.left', {
          name: 'conteudoEncontrado',
          value: 'false'
        })
        ]),
        m('label', {
        for: 'opiniao'
      }, [
            m('h3.opiniao', 'Quais informações você sentiu falta neste serviço?'),
            m('textarea#opiniao.row', {
          name: 'mensagem',
          rows: 5
        }),
        m('input[type=submit].acao', {
          name: 'enviar',
          value: 'ENVIAR'
        })
        ])
    ]);
  }
};