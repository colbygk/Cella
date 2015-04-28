%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out maximum fitnesses
%

clearvars;
clf;

hold off;

d = 'test2';
fnt = 20;
fnl = 18;
fnm = 15;

logs = dir(sprintf('%s/run49.log.fitness.log',d));

cnt = 0;
y=[];

f1=figure(1);
hold on;
for l = logs'
    x=load(sprintf('%s/%s',d,l.name));
    if ( max(x(:,1)) > 90 )
        if ( length(y) > 0 )
            y = cat(2,y,x(:,1));
        else
            y = x(:,1);
        end
        cnt = cnt + 1;
        disp(l.name);
        plot( y );
        title('Max Fitnesses Run 49 (radius 2)','FontSize',fnt,'Interpreter','Latex');
        xlabel('Generation','FontSize',fnl,'Interpreter','Latex');
        ylabel('Maximum fitness','FontSize',fnl,'Interpreter','Latex');
        set(gca,'FontSize',fnm);
    end
end


% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'max_epoch_radius2.eps','psc2');
