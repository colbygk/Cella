%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out the mutational robustness
%


clearvars;
clf;

fnt = 20;
fnl = 18;
fnx = 15;
fnlg = 20;

x = load('data/mutations_elite.dat');
overall_max = max( x(:) );

mutations = x(:,1)';
x(:,1) = [];
y = (x')./overall_max;

f1=figure(1);
hold off;
errorbar( mutations, mean(y), std(y) );
hold on;
plot( mutations, max(y) );

ylim([0.0 1.1]);
xlabel( 'Number of mutations', 'Interpreter','Latex','FontSize',fnl );
ylabel( 'Fitness', 'Interpreter','Latex','FontSize',fnl );
title( 'Mutational Robustness', 'Interpreter','Latex','FontSize',fnt  );
legend( 'Mean Fitness','Max Fitness');
set(gca, 'FontSize', fnx );

% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'mutational_robustness.eps','psc2');
