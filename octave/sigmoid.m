function g = sigmoid(z)
%SIGMOID Compute sigmoid function
%   g = SIGMOID(z) computes the sigmoid of z.

g = zeros(size(z));

A = exp(-z);
B = 1 + A;
g = 1 ./ B;
%g = 1 ./ (1 + exp(-z)); not working for some reason


end
