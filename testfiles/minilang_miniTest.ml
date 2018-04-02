//this not read
declaration: x, y

fun max(a, b, c)
{
    do
        a > b -> a, b := b, a
    |   b > c -> b, c := c, b
    od
    return c
}
print(max(3,2,5))

x := 1
y := 2
x, y := y, x
print(x)
print(y)
if
x > 0 -> print(x) | x > 1 -> {print(-x) x := x + 1} |
x > 2 -> {if 1 > 2 -> print(1) | 2 > 1 -> print(2) | 2 > 1 -> print(3) fi x := 0} fi
x := 3
