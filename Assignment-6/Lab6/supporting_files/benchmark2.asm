.data
a:
    1
    2
    3
    4
    5
.text
main:
    addi %x0, 1000, %x3
    addi %x0, 1, %x4
loop:
    beq %x3, %x0, end
    sub %x5, %x5, %x5
    load %x5, $a, %x6
    add %x4, %x5, %x5
    load %x5, $a, %x7
    add %x4, %x5, %x5
    load %x5, $a, %x8
    add %x4, %x5, %x5
    load %x5, $a, %x9
    add %x4, %x5, %x5
    load %x5, $a, %x10
    add %x4, %x5, %x5
    sub %x3, %x4, %x3
    jmp loop
end:
    end