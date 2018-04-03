//this not read
declaration: x, y, z, w

fun max(a, b, c)
{
    do
        a > b -> a, b := b, a
    |   b > c -> b, c := c, b
    od
    return c
}

fun fibonacci(x)
{
    if
        x <= 1 -> return x
    |   x > 1 -> return fibonacci(x - 1) + fibonacci (x - 2)
    fi
}
print(max(3,2,5))
print(fibonacci(10))
x := 3
y := 5
z := -2
w := 32

do
    x > y -> x, y := y, x
|   y > z -> y, z := z, y
|   z > w -> z, w := w, z
od
x, y, z, w := w, z, y, x
print(x)
print(y)
print(z)
print(w)