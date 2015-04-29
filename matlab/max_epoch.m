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

d = 'data/biased_r3';
fnt = 20;
fnl = 18;
fnm = 15;

logs = dir(sprintf('%s/run39.fitness.log',d));

cnt = 0;
y=[];
leg={};
f1=figure(1);
hold on;
for l = logs'
    x=load(sprintf('%s/%s',d,l.name));
    cnt = cnt + 1;
    if ( max(x(:,1)) > 95 )
        if ( length(y) > 0 )
            y = cat(2,y,x(:,1));
        else
            y = x(:,1);
        end
        disp(sprintf('%s - norm:%f', l.name, norm(x(:,1))));
        p=plot( y );
        title(sprintf('Max Fitnesses Run %d (radius 3)',cnt),'FontSize',fnt,...
            'Interpreter','Latex');
        xlabel('Generation','FontSize',fnl,'Interpreter','Latex');
        ylabel('Maximum fitness','FontSize',fnl,'Interpreter','Latex');
        set(gca,'FontSize',fnm);
        leg{end+1} = l.name;
    end
end
legend(leg,'Location','Southeast' );


% Figure saving on OSX seems to be broken in recent versions.
% this fills a landscape eps.
%f1.PaperOrientation = 'landscape';
%f1.PaperUnits = 'centimeters';
%f1.PaperPosition = [-1.25 1 30 20];
%saveas(f1,'max_epoch_radius2.eps','psc2');
