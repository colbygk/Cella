%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out the mutational robustness
%


clearvars;
clf;

x = load('tst');

mutations = x(:,1)';
x(:,1) = [];
y = x';

hold off;
plot( mutations, mean(y) );
hold on;
plot( mutations, max(y) );