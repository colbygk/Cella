%
% Project 2
% CS 523 Spring 2015
% 
% Colby & Whit
%   Plot out mean and median fitnesses
%

clearvars;

hold off;

d = 'test2';

logs = dir(sprintf('%s/run*.log.fitness.log',d));

cnt = 0;
y=[];

for l = logs'
    x=load(sprintf('%s/%s',d,l.name));
    if ( max(x(:,1)) > 80 )
        if ( length(y) > 0 )
            y = cat(2,y,x(:,1));
        else
            y = x(:,1);
        end
        cnt = cnt + 1;
    end
end

figure(1)
plot(median(y,2));
%hold on;
%plot(std(y,[],2));
figure(2);
plot(mean(y,2));