/**
 * Module dependencies.
 */
var mongoose = require('mongoose'),
    _ = require('underscore');

/* Home View */
exports.index = function(req, res) {
  res.render('home');
};
