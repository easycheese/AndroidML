function [J, grad] = lrCostFunction(theta, X, y, lambda)
%LRCOSTFUNCTION Compute cost and gradient for logistic regression with 
%regularization
%   J = LRCOSTFUNCTION(theta, X, y, lambda) computes the cost of using
%   theta as the parameter for regularized logistic regression and the
%   gradient of the cost w.r.t. to the parameters. 

m = length(y); % number of training examples
J = 0;
grad = zeros(size(theta));


h = sigmoid(X*theta);
theta(1,1) = 0; %set first term to 0 since we don't want to regularize it

A = (-y' * log(h));
A = (-y' * log(h));
B = ((1 - y)' * log(1 - h));
C = lambda/(2*m) * (theta' * theta);
% Only reg difference is + C term
J = 1/m * (A - B) + C;
%J = 1/m * (A - B);

% only reg diff is lambda * theta_reg
grad = (1/m)*(X'*(h-y)+lambda*theta);
%grad = (1/m)*(X'*(h-y));

grad = grad(:);

end
