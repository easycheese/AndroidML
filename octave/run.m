train_set = ceil(loadMNISTImages('train-images.idx3-ubyte'))';
train_label = loadMNISTLabels('train-labels.idx1-ubyte');
test_set = ceil(loadMNISTImages('t10k-images.idx3-ubyte'))';
test_label = loadMNISTLabels('t10k-labels.idx1-ubyte');
num_labels = 10;
lambda = 0.0;

imagesc(reshape(train_set(16, :), 28, 28));

[m, n] = size(train_set);
X = [ones(m, 1) train_set];
y = train_label;


[mTest, nTest] = size(test_set);
Xtest = [ones(mTest, 1) test_set];
yTest = test_label;


options = optimset('GradObj', 'on', 'MaxIter', 100);

n = size(X, 2);
all_theta = zeros(num_labels, n);

for c = 1:num_labels
	initial_theta = zeros(n, 1);
	%all_theta(c,:) =  fminunc (@(t)(lrCostFunction(t, X, (y == c), lambda)), initial_theta, options);
	all_theta(c,:) =  fmincg (@(t)(lrCostFunction(t, X, (y == c), lambda)), initial_theta, options);
endfor


pred = predictOneVsAll(all_theta, X);  
fprintf('\nTraining Set Accuracy: %f\n', mean(double(pred == y)) * 100);

predTest = predictOneVsAll(all_theta, Xtest);
fprintf('\nTest Set Accuracy: %f\n', mean(double(predTest == yTest)) * 100);

output = all_theta';
csvwrite("theta-output.csv",output);