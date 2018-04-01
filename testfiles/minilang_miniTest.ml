//this not read
declaration: x, y

//fun sort(a, b, c)
//{
//    do
//        a > b ->
//}


x := 1
y := 2
x, y := y, x
print(x)
print(y)
if
x > 0 -> print(x) | x > 1 -> {print(-x) x := x + 1} |
x > 2 -> {if 1 > 2 -> print(1) | 2 > 1 -> print(2) | 2 > 1 -> print(3) fi x := 0} fi
x := 3
print(x*2)