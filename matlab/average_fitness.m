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

d = 'test3';
fnt = 20;
fnl = 18;
fnm = 15;

logs = dir(sprintf('%s/run45.log.fitness.log',d));

cnt = 0;
y=[];
averages=[];
variation=[];
deviation=[];

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
        for i = 1:50
            M = mean(x(i,:));
            V = var(x(i,:));
            D = std(x(i,:));
            averages = [averages M];
            variation = [variation V];
            deviation = [deviation D];
        end
        cnt = cnt + 1;
        disp(l.name);
        plot( y );
        plot( averages );
        %errorbar(averages, deviation);
        legend('Max Fitness', 'Mean Fitness', 'Location','SouthEast')
        title('Max and Mean Fitness Run 45 (radius 3)','FontSize',fnt,'Interpreter','Latex');
        xlabel('Generation','FontSize',fnl,'Interpreter','Latex');
        ylabel('Fitness','FontSize',fnl,'Interpreter','Latex');
        set(gca,'FontSize',fnm);

     end
end


% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
f1.PaperOrientation = 'landscape';
f1.PaperUnits = 'centimeters';
f1.PaperPosition = [-1.25 1 30 20];
saveas(f1,'max_epoch_radius2.eps','psc2');
