x=load('test.log')';

ax = 0:0.1:1;

figure(1);
subplot(3,2,1);
histogram(x(:,1),15);
ylim([0 15] );
xlim([0 1]);
title('Generation 0');

for k=1:5
    subplot(3,2,k+1);
    histogram(x(:,10*k),15);
    ylim([0 15] );
    xlim([0 1]);
    title(sprintf( 'Generation %d', 10*k));
end


elites=load('elites.test.log')';

figure(2);
subplot(3,2,1);
histogram(elites(:,1),15);
ylim([0 6] );
xlim([0 1]);
title('Generation 0');

for k=1:5
    subplot(3,2,k+1);
    histogram(elites(:,10*k),15);
    ylim([0 6] );
    xlim([0 1]);
    title(sprintf( 'Generation %d', 10*k));
end
