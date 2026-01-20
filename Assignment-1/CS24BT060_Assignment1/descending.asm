	.data
a:
	67
    420
    69
    123
    -321
    100000000
    -999999
n:
	7
	.text
main:
    load %x0, $n, %x3
    subi $x3, 1, %x3
    add %x4, %x0, %x4
    jmp loop1
loop1:
    beq %x4, %x3, endl1
    add %x0, %0, %x5
    sub %x3, %x4, %x7
    jmp loop2
loop2:
    beq %x5, %x7, endl2
    addi %x5, 1, %x6
    load %x5, $a, %x8
    load %x6, $a, %x9
    bgt %x9, %x8, swap
    addi %x5, 1, %x5
    jmp loop2
swap:
    store %x9, $a, %x5
    store %x8, $a, %x6
    addi %x5, 1, %x5
    jmp loop2
endl2:
    addi $x4, 1, %x4
    jmp loop1
endl1:
    end