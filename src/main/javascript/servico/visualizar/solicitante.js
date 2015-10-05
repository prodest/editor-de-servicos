'use strict';

module.exports = {

  controller: function (args) {
    this.solicitantes = args;
    this.converter = new window.showdown.Converter();
  },

  view: function (ctrl) {
    return m('', ctrl.solicitantes.map(function (s) {
      return m('.solicitantes markdown margem-solicitantes', [
                m('h4', m.trust(s.tipo())),
                m.trust(ctrl.converter.makeHtml(s.requisitos()))
            ]);
    }));
  }
};
