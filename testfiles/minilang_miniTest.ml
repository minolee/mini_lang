//this not read
declaration: x
x := 1
if
x > 0 -> print(x) | x > 1 -> {print(-x) x := x + 1} |
x > 2 -> {if 1 > 2 -> print(1) | 2 > 1 -> print(2) | 2 > 1 -> print(3) fi x := 0} fi
x := 3
print(x*2)