%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out rule lambda histograms
%

clearvars;

x=load('test.log')';

ax = 0:0.1:1;
nbins = [0:0.05:1 1];
ylt = 50;
yl = 10;
fnt = 20;
fnl = 18;

figure(1);
subplot(3,2,1);
histogram(x(:,1),nbins);
set(gca,'FontSize',15);
ylim([0 ylt] );
xlim([0 1]);
title('Generation 0','FontSize',fnt,'Interpreter','Latex');
ylabel('Count','FontSize',fnl,'Interpreter','Latex');

for k=1:5
    subplot(3,2,k+1);
    histogram(x(:,10*k),nbins);
    set(gca,'FontSize',15);
    ylim([0 ylt] );
    xlim([0 1]);
    title(sprintf( 'Generation %d', 10*k),'FontSize',fnt,'Interpreter','Latex');
    if ( mod(k,2) == 0 )
        ylabel('Count','FontSize',fnl,'Interpreter','Latex');
    end
    
    if ( k > 3 )
        xlabel( '$\lambda$','FontSize',fnl,'Interpreter','Latex' );
    end
end


elites=load('elites.test.log')';

figure(2);
title('Elite lambdas');
subplot(3,2,1);
histogram(elites(:,1),nbins);
set(gca,'FontSize',15);
ylim([0 yl] );
xlim([0 1]);
title('Generation 0','FontSize',fnt,'Interpreter','Latex');
ylabel('Count','FontSize',fnl,'Interpreter','Latex');

for k=1:5
    subplot(3,2,k+1);
    histogram(elites(:,10*k),nbins);
    set(gca,'FontSize',15);
    ylim([0 yl] );
    xlim([0 1]);
    title(sprintf( 'Generation %d', 10*k),'FontSize',fnt,'Interpreter','Latex');
    
    if ( mod(k,2) == 0 )
        ylabel('Count','FontSize',fnl,'Interpreter','Latex');
    end
    
    if ( k > 3 )
        xlabel( '$\lambda$','FontSize',fnl,'Interpreter','Latex' );
    end
end
