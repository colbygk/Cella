%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out fraction of success vs \rho_0 \in [0.0 1.0]
%

clearvars;
clf;

fnt = 20;
fnl = 18;
fnx = 15;

x=load('data/lambda056.dat');

f1=figure(1);
plot(x(:,1),x(:,2));
xlim([0.0 1.0]);
ylim([0.0 1.0]);
xlabel('$\rho_0$','Interpreter','Latex','FontSize',fnl);
ylabel('Fraction of correct classifications','Interpreter','Latex',...
    'FontSize',fnl);
title('Correctness of Rule, radius ',...
    'Interpreter','Latex','FontSize',fnt);
set(gca,'FontSize',fnx);

hold on;

x=load('data/lambda05.dat');

plot(x(:,1),x(:,2));
xlim([0.0 1.0]);
ylim([0.0 1.0]);
xlabel('$\rho_0$','Interpreter','Latex','FontSize',fnl);
ylabel('Fraction of correct classifications','Interpreter','Latex',...
    'FontSize',fnl);
set(gca,'FontSize',fnx);

legend( '\lambda \approx 0.5625', '\lambda = 0.5','Location','Southeast');
% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'lambda_correctness_1.eps','psc2');
