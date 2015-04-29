%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out entropy vs fitness
%

clearvars;
clf;

fnt = 20;
fnl = 18;
fnx = 15;

d=load('tst');
x = d(:,2)';
y = d(:,1)';

x_max = max( x(:) );
y_max = max( y(:) );

px = x./x_max;
py = y./y_max;

f1=figure(1);
scatter(sort(px),py);
xlabel('$\rho_0$','Interpreter','Latex','FontSize',fnl);
ylabel('Fraction of correct classifications','Interpreter','Latex',...
    'FontSize',fnl );
title('Relation of Shannon Entropy to Fitness','Interpreter','Latex',...
    'FontSize',fnt);
xlabel( 'fitness')
ylabel( 'entropy')
ylim([0.5 1.1])
xlim([-0.1 1.1])


legend( 'r=3',...
    'Location','Southeast');
set(gca,'FontSize',fnx);

% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'entropy_fitness_plot.eps','psc2');
