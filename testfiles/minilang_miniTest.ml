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

fun sum(start, end)
{
    if
        start == end -> return end
    |   start < end -> return end + sum(start, end - 1)
    fi
}

fun A(x)
    return x
fun B(x)
    if x > 1 -> return plus(B(x - 1), -1)
    | x <= 1 -> return x
    fi
fun plus(x, y)
    return x + y
fun fibonacci(x)
{
    if
        x <= 1 -> return 1
    |   x > 1 -> { declaration: y, z y, z := fibonacci(x - 1), fibonacci(x - 2) print(0) print(y) print(z) return y + z}
    fi
}
//print(B(10))
print(sum(1, 10))
//print(max(3,2,5))
x := 10
print(fibonacci(x))
do
    //x > 0 -> {print(x) x := x - 1}
od
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