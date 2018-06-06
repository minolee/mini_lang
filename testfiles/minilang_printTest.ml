declaration: a, b, c
//expr print test
a := 1*2+3/4==5<c+x(1,2,3)

//if statement test
if
    a>1 -> a := a - 1
|   a<1 -> abort
fi

//concurrent statement test
a,b := b,(a+2)*3

//complex test
do
    b >= 12 + (3 * 4 - 6) -> if c > 1 -> c := c + 1 | c < 1 -> skip fi
|   a < 3%4+5+-6 -> abort
od
