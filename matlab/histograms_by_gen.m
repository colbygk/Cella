%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out rule lambda histograms for elite rules
%

clearvars;

d = 'test3';
step = 5;
sr = 3;
sc = ceil((50/step)/3);

logs = dir(sprintf('%s/run*.log.elite.lambda.log',d));

ax = 0:0.1:1;
nbins = [0:0.05:1 1];
ylt = 50;
yl = 125;
fnt = 15;
fnl = 15;
count = 0;

lambdas = [];
figure(1);
title('Elite Lambda Values');
for k = 0:step:50
    k
    for l = logs'
        contents = load(sprintf('%s/%s',d,l.name));
        % first histogram is generation 1 (whcih is line 2 in the log)
        if (isempty(lambdas) && k == 0)
            lambdas = contents(2,:);
        elseif (~isempty(lambdas) && k == 0)
            lambdas = cat(2,lambdas,contents(2,:));
        elseif (isempty(lambdas) && k > 0)
            lambdas = contents(k,:);
        else
            lambdas = cat(2,lambdas,contents(k,:));
        end
    end
    count = count + 1;
    subplot(sc,sr,count);
    histogram(lambdas, nbins, 'FaceColor', [0.2 0.7 0.7]);
    set(gca,'FontSize',12);
    ylim([0 yl] );
    xlim([0 1]);
    % hack to lable the first histogram correctly
    if (k == 0) 
        gen = 1;
    else
        gen = k;
    end
    title(sprintf('Generation %d',gen),'FontSize',fnt,'Interpreter','Latex');
    ylabel('Count','FontSize',fnl,'Interpreter','Latex');
    xlabel('\lambda', 'FontSize', fnl);
    length(lambdas)
    lambdas = [];
end
return
