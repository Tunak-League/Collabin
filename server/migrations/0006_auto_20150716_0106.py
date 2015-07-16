# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('server', '0005_auto_20150707_2027'),
    ]

    operations = [
        migrations.CreateModel(
            name='UserProfiles',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('user_summary', models.CharField(max_length=500)),
                ('location', models.CharField(max_length=30)),
                ('image_path', models.CharField(default=None, max_length=50, null=True, blank=True)),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.AlterField(
            model_name='projects',
            name='id_owner',
            field=models.ForeignKey(to='server.UserProfiles'),
        ),
        migrations.AlterField(
            model_name='skills',
            name='users',
            field=models.ManyToManyField(to='server.UserProfiles'),
        ),
        migrations.AlterField(
            model_name='types',
            name='users',
            field=models.ManyToManyField(to='server.UserProfiles'),
        ),
        migrations.DeleteModel(
            name='Users',
        ),
    ]
