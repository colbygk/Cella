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

x=load('data/rho0_correctness_r2.dat');

f1=figure(1);
plot(x(:,1),x(:,2));
xlim([0.0 1.0]);
ylim([0.0 1.0]);
xlabel('$\rho_0$','Interpreter','Latex','FontSize',fnl);
ylabel('Fraction of correct classifications','Interpreter','Latex',...
    'FontSize',fnl );
title('Correctness of Elite Rules','Interpreter','Latex',...
    'FontSize',fnt);

hold on;

x=load('data/rho0_correctness_r3.dat');
plot(x(:,1),x(:,2),'-.');

x=load('data/rho0_biased_correctness_r2.dat');

plot(x(:,1),x(:,2),'-o');
xlim([0.0 1.0]);
ylim([0.0 1.0]);
xlabel('$\rho_0$','Interpreter','Latex','FontSize',fnl);
ylabel('Fraction of correct classifications','Interpreter','Latex',...
    'FontSize',fnl );
title('Correctness of Elite Rules','Interpreter','Latex',...
    'FontSize',fnt);

x=load('data/rho0_biased_correctness_r3.dat');
plot(x(:,1),x(:,2),'--');

legend( 'r=2, \lambda = 0.5625', 'r=3, \lambda \approx 0.546875',...
    'r=2, \lambda = 0.5625 biased', 'r=3, \lambda \approx 0.546875 biased',...
    'Location','Southeast');
set(gca,'FontSize',fnx);

% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'lambda_correctness_plot.eps','psc2');
